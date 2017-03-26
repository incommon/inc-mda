
package uk.org.iay.incommon.mda.dom.saml;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.dom.DOMElementItem;
import net.shibboleth.metadata.dom.saml.mdrpi.RegistrationAuthority;
import uk.org.iay.incommon.mda.dom.saml.InCommonEntityOrderingStrategy;
import uk.org.ukfederation.mda.BaseDOMTest;

public class InCommonEntityOrderingStrategyTest extends BaseDOMTest {

    protected InCommonEntityOrderingStrategyTest() {
        super(InCommonEntityOrderingStrategy.class);
    }

    private Item<Element> makeItem(String registrar, String entityID) throws Exception {
        final Item<Element> item = new DOMElementItem(readXmlData("trivial.xml"));
        item.getItemMetadata().put(new ItemId(entityID));
        item.getItemMetadata().put(new RegistrationAuthority(registrar));
        return item;
    }

    @Test
    public void testOrder() throws Exception {
        // distinguished registrar
        final String registrar_d = "http://d";

        // other registrars
        final String registrar_1 = "http://registrar1";
        final String registrar_2 = "http://registrar2";

        // create some items in the order they will end up
        final Item<Element> i00 = makeItem(registrar_d, "entity-id-999");
        final Item<Element> i01 = makeItem(registrar_d, "entity-id-777");
        final Item<Element> i10 = makeItem(registrar_1, "entity-id-0");
        final Item<Element> i11 = makeItem(registrar_1, "entity-id-5");
        final Item<Element> i12 = makeItem(registrar_1, "entity-id-9");
        final Item<Element> i20 = makeItem(registrar_2, "entity-id-a");
        final Item<Element> i21 = makeItem(registrar_2, "entity-id-m");
        final Item<Element> i22 = makeItem(registrar_2, "entity-id-z");

        // Make a collection containing those items in an arbitrary order
        // Note that the registrar_d items must be in the final order
        final List<Item<Element>> items = new ArrayList<>();
        items.add(i22);
        items.add(i11);
        items.add(i00);
        items.add(i12);
        items.add(i20);
        items.add(i21);
        items.add(i01);
        items.add(i10);
        Assert.assertEquals(items.size(), 8);

        // Order the collection
        final InCommonEntityOrderingStrategy strat = new InCommonEntityOrderingStrategy(registrar_d);
        final List<Item<Element>>items2 = strat.order(items);

        // Check that everything is in the right place afterwards
        Assert.assertEquals(items2.size(), items.size());
        Assert.assertEquals(items2.get(0), i00, "i00");
        Assert.assertEquals(items2.get(1), i01, "i01");
        Assert.assertEquals(items2.get(2), i10, "i10");
        Assert.assertEquals(items2.get(3), i11, "i11");
        Assert.assertEquals(items2.get(4), i12, "i12");
        Assert.assertEquals(items2.get(5), i20, "i20");
        Assert.assertEquals(items2.get(6), i21, "i21");
        Assert.assertEquals(items2.get(7), i22, "i22");
    }

}
