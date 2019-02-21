
package uk.org.iay.incommon.mda.validate.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.metadata.validate.Validator.Action;
import uk.org.iay.incommon.mda.validate.BaseLocalValidator;
import uk.org.ukfederation.mda.MockItem;

public class AsLiteralTailStringValidatorTest {

    private static class CountingCapturingValidator extends BaseLocalValidator implements Validator<String> {
        public int count;
        public String value;
        private final Action action;

        @Override
        public Action validate(String e, Item<?> item, String stageId) throws StageProcessingException {
            count++;
            value = e;
            return action;
        }

        /** Constructor. */
        public CountingCapturingValidator(final Action a) {
            action = a;
        }
    }

    @Test
    public void testAssumptions() throws Exception {
        final Pattern pattern = Pattern.compile(".*?\\\\.(([a-zA-Z0-9-]+\\\\.)+[a-zA-Z0-9-]+)\\$");
        final String value = "^([a-zA-Z0-9-]{1,63}\\.){0,2}vho\\.aaf\\.edu\\.au$";
        final Matcher matcher = pattern.matcher(value);
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testExample() throws Exception {
        final CountingCapturingValidator ccv = new CountingCapturingValidator(Action.CONTINUE);
        ccv.setId("ccv");
        ccv.initialize();

        final List<Validator<String>> nvs = new ArrayList<>();
        nvs.add(ccv);

        final AsLiteralTailStringValidator val = new AsLiteralTailStringValidator();
        val.setId("val");
        val.setValidators(nvs);
        val.initialize();

        final Item<String> item = new MockItem("content");
        Assert.assertEquals(val.validate("^([a-zA-Z0-9-]{1,63}\\.){0,2}vho\\.aaf\\.edu\\.au$", item, "stage"), Action.CONTINUE);
        Assert.assertEquals(ccv.count, 1);
        Assert.assertEquals(ccv.value, "aaf.edu.au");
    }

    /*
     * Example from the REFEDS MRPS template document.
     *
     * See https://github.com/REFEDS/MRPS/blob/master/MRPS-templatev1.1.pdf
     */
    @Test
    public void testREFEDSExample() throws Exception {
        final CountingCapturingValidator ccv = new CountingCapturingValidator(Action.CONTINUE);
        ccv.setId("ccv");
        ccv.initialize();

        final List<Validator<String>> nvs = new ArrayList<>();
        nvs.add(ccv);

        final AsLiteralTailStringValidator val = new AsLiteralTailStringValidator();
        val.setId("val");
        val.setValidators(nvs);
        val.initialize();

        final Item<String> item = new MockItem("content");
        Assert.assertEquals(val.validate("^(foo|bar)\\.example\\.com$", item, "stage"), Action.CONTINUE);
        Assert.assertEquals(ccv.count, 1);
        Assert.assertEquals(ccv.value, "example.com");
    }
}
