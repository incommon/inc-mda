/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.org.iay.incommon.mda.dom.saml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import net.shibboleth.metadata.Item;
import net.shibboleth.metadata.ItemId;
import net.shibboleth.metadata.dom.saml.mdrpi.RegistrationAuthority;
import net.shibboleth.metadata.pipeline.ItemOrderingStrategy;
import net.shibboleth.metadata.pipeline.StageProcessingException;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullElements;

/**
 * Implements an ordering strategy for InCommon federation aggregates.
 *
 * The input list contains entities registered by InCommon in a specific
 * order which must not be disturbed. They are followed by entities registered
 * elsewhere; these are to be grouped by registrar and then within registrar
 * ordered by entityID.
 * 
 * The registrar and entityID values used for ordering are required to be
 * present in the item's item metadata.
 *
 * @param <T> type of item to be handled
 */
@ThreadSafe
public class InCommonEntityOrderingStrategy<T> implements ItemOrderingStrategy<T> {

    /** Class logger. */
    private final Logger log = LoggerFactory.getLogger(InCommonEntityOrderingStrategy.class);

    /** The registrar whose entities should always appear first, in the provided order. */

    private final String distinguishedRegistrar;

    /**
     * Helper class which wraps an {@link Item} but extracts any
     * associated {@link RegistrationAuthority} and {@link ItemId} for simpler comparisons.
     *
     * @param <T> type of item to be handled
     */
    private static class OrderableItem<T> implements Comparable<OrderableItem> {

        /** The wrapped {@link Element} {@link Item}. */
        private final Item<T> item;

        /** The registrar for this entity. */
        private final String registrar;

        /** The entityID for this entity. */
        private final String entityID;

        /**
         * Constructor.
         * 
         * @param domItem the {@link Element} {@link Item} to wrap
         * @param reg the registrar for this entity
         * @param entity the entityID for this entity
         */
        public OrderableItem(@Nonnull final Item<T> domItem,
                @Nonnull final String reg, @Nonnull final String entity) {
            item = domItem;
            registrar = reg;
            entityID = entity;
        }

        @Override
        public int compareTo(@Nonnull final OrderableItem o) {
            // compare registrar values
            final int c = registrar.compareTo(o.registrar);
            // done if unequal
            if (c != 0) {
                return c;
            }
            // compare entityID values if registrars equal
            return entityID.compareTo(o.entityID);
        }

        /**
         * Unwrap the wrapped {@link Element} {@link Item}.
         * 
         * @return the wrapped {@link Element} {@link Item}.
         */
        public Item<T> unwrap() {
            return item;
        }
    }

    /**
     * Constructor.
     *
     * @param registrar distinguished registrar, whose entities sort first
     */
    public InCommonEntityOrderingStrategy(@Nonnull final String registrar) {
        distinguishedRegistrar = registrar;
    }

    @Override
    public List<Item<T>> order(@Nonnull @NonnullElements final List<Item<T>> items) {

        // Collect the results here
        final List<Item<T>> results = new ArrayList<>(items.size());

        /*
         * Construct an orderable list wrapping the original items.
         * 
         * Any belonging to the distinguished registrar are instead put straight in the
         * results list.
         */
        final List<OrderableItem<T>> orderableList = new ArrayList<>(items.size());
        try {
            for (final Item<T> item : items) {
                final List<RegistrationAuthority> registrars = item.getItemMetadata().get(RegistrationAuthority.class);
                final String registrar;
                if (registrars.size() == 0) {
                    throw new StageProcessingException("entity does not have RegistrationAuthority item metadata");
                } else {
                    registrar = registrars.get(0).getRegistrationAuthority();
                    if (distinguishedRegistrar.equals(registrar)) {
                        results.add(item);
                        continue;
                    }
                }

                final List<ItemId> itemids = item.getItemMetadata().get(ItemId.class);
                final String entityID;
                if (itemids.size() == 0) {
                    throw new StageProcessingException("entity does not have ItemId item metadata");
                } else {
                    entityID = itemids.get(0).getId();
                }

                orderableList.add(new OrderableItem(item, registrar, entityID));
            }
        } catch (final StageProcessingException e) {
            /*
             * If the ordering operation fails because we can't create an OrderableItem
             * for each original item, it's probably because we are missing some item
             * metadata on one or more items. The signature for the order method does
             * not allow this to be reported as of MDA 0.9.x.
             *
             * This will change in MDA 0.10.0 (see IDP-175) but for now all we
             * can really do is log an error and return the items in their
             * original order.
             */
            log.error("could not order entities because of lack of entity metadata; returning original ordering");
            return new ArrayList<>(items);
        }

        // sort the orderable list
        Collections.sort(orderableList);

        // Add the ordered results into the results collection
        for (final OrderableItem result : orderableList) {
            results.add(result.unwrap());
        }

        return results;
    }

}
