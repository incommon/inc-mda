
package uk.org.iay.incommon.mda.validate;

import java.util.List;

import org.testng.annotations.Test;

import junit.framework.Assert;
import net.shibboleth.metadata.ErrorStatus;
import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.validate.Validator;
import net.shibboleth.metadata.validate.Validator.Action;
import uk.org.ukfederation.mda.MockItem;

public class AlwaysAcceptValidatorTest {

    @Test
    public void validate() throws Exception {
        final AlwaysAcceptValidator v = new AlwaysAcceptValidator();
        v.setId("comp");
        v.initialize();

        final Item<String> item = new MockItem("test");
        final Validator.Action action = v.validate("foo", item, "stage");
        Assert.assertEquals(Action.DONE, action);

        final List<ErrorStatus> errs = item.getItemMetadata().get(ErrorStatus.class);
        Assert.assertEquals(0, errs.size());
    }

}
