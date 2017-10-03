
package uk.org.iay.incommon.mda.validate;

import java.util.List;

import org.testng.annotations.Test;

import junit.framework.Assert;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.metadata.validate.Validator.Action;
import uk.org.ukfederation.mda.MockItem;

public class AlwaysRejectValidatorTest {

    @Test
    public void validate() throws Exception {
        final AlwaysRejectValidator v = new AlwaysRejectValidator();
        v.setId("comp");
        v.initialize();

        final Item<String> item = new MockItem("test");
        final Validator.Action action = v.validate("foo", item, "stage");
        Assert.assertEquals(Action.DONE, action);

        final List<ErrorStatus> errs = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(1, errs.size());

        final ErrorStatus err = errs.get(0);
        Assert.assertEquals("stage/comp", err.getComponentId());
        Assert.assertEquals("value rejected: 'foo'", err.getStatusMessage());
    }

    @Test
    public void validateWithMessage() throws Exception {
        final AlwaysRejectValidator v = new AlwaysRejectValidator();
        v.setId("comp");
        v.setMessage("decimal %.2f");
        v.initialize();

        final Item<String> item = new MockItem("test");
        final Validator.Action action = v.validate(new Double(1.25), item, "stage");
        Assert.assertEquals(Action.DONE, action);

        final List<ErrorStatus> errs = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(1, errs.size());

        final ErrorStatus err = errs.get(0);
        Assert.assertEquals("stage/comp", err.getComponentId());
        Assert.assertEquals("decimal 1.25", err.getStatusMessage());
    }

}
