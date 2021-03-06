/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.ui;

import com.vaadin.data.Listing;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.server.data.DataGenerator;
import com.vaadin.server.data.DataSource;
import com.vaadin.shared.ui.optiongroup.RadioButtonGroupConstants;
import com.vaadin.shared.ui.optiongroup.RadioButtonGroupState;
import elemental.json.JsonObject;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A group of RadioButtons. Individual radiobuttons are made from items supplied by
 * a {@link DataSource}. RadioButtons may have captions and icons.
 *
 * @param <T>
 *            item type
 * @author Vaadin Ltd
 * @since 8.0
 */
public class RadioButtonGroup<T> extends AbstractSingleSelect<T> {

    private Function<T, Resource> itemIconProvider = item -> null;

    private Function<T, String> itemCaptionProvider = String::valueOf;

    private Predicate<T> itemEnabledProvider = item -> true;

    /**
     * Constructs a new RadioButtonGroup with caption.
     *
     * @param caption
     *            caption text
     * @see Listing#setDataSource(DataSource)
     */
    public RadioButtonGroup(String caption) {
        this();
        setCaption(caption);
    }

    /**
     * Constructs a new RadioButtonGroup with caption and DataSource.
     *
     * @param caption
     *            the caption text
     * @param dataSource
     *            the data source, not null
     * @see Listing#setDataSource(DataSource)
     */
    public RadioButtonGroup(String caption, DataSource<T> dataSource) {
        this(caption);
        setDataSource(dataSource);
    }

    /**
     * Constructs a new RadioButtonGroup with caption and DataSource containing
     * given items.
     *
     * @param caption
     *            the caption text
     * @param items
     *            the data items to use, not null
     * @see Listing#setDataSource(DataSource)
     */
    public RadioButtonGroup(String caption, Collection<T> items) {
        this(caption, DataSource.create(items));
    }

    /**
     * Constructs a new RadioButtonGroup.
     *
     * @see Listing#setDataSource(DataSource)
     */
    public RadioButtonGroup() {
        setSelectionModel(new SimpleSingleSelection());

        addDataGenerator(new DataGenerator<T>() {
            @Override
            public void generateData(T data, JsonObject jsonObject) {
                jsonObject.put(RadioButtonGroupConstants.JSONKEY_ITEM_VALUE,
                        itemCaptionProvider.apply(data));
                Resource icon = itemIconProvider.apply(data);
                if (icon != null) {
                    String iconUrl = ResourceReference
                            .create(icon, RadioButtonGroup.this, null).getURL();
                    jsonObject.put(RadioButtonGroupConstants.JSONKEY_ITEM_ICON,
                            iconUrl);
                }
                if (!itemEnabledProvider.test(data)) {
                    jsonObject.put(RadioButtonGroupConstants.JSONKEY_ITEM_DISABLED,
                            true);
                }

                if (getSelectionModel().isSelected(data)) {
                    jsonObject.put(RadioButtonGroupConstants.JSONKEY_ITEM_SELECTED,
                            true);
                }
            }

            @Override
            public void destroyData(T data) {
            }
        });

    }

    /**
     * Sets whether html is allowed in the item captions. If set to true, the
     * captions are passed to the browser as html and the developer is
     * responsible for ensuring no harmful html is used. If set to false, the
     * content is passed to the browser as plain text.
     *
     * @param htmlContentAllowed
     *            true if the captions are used as html, false if used as plain
     *            text
     */
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        getState().htmlContentAllowed = htmlContentAllowed;
    }

    /**
     * Checks whether captions are interpreted as html or plain text.
     *
     * @return true if the captions are used as html, false if used as plain
     *         text
     * @see #setHtmlContentAllowed(boolean)
     */
    public boolean isHtmlContentAllowed() {
        return getState(false).htmlContentAllowed;
    }

    @Override
    protected RadioButtonGroupState getState() {
        return (RadioButtonGroupState) super.getState();
    }

    @Override
    protected RadioButtonGroupState getState(boolean markAsDirty) {
        return (RadioButtonGroupState) super.getState(markAsDirty);
    }

    /**
     * Returns the item icons provider.
     *
     * @return the icons provider for items
     * @see #setItemIconProvider
     */
    public Function<T, Resource> getItemIconProvider() {
        return itemIconProvider;
    }

    /**
     * Sets the item icon provider for this radiobutton group. The icon provider is
     * queried for each item to optionally display an icon next to the item
     * caption. If the provider returns null for an item, no icon is displayed.
     * The default provider always returns null (no icons).
     *
     * @param itemIconProvider
     *            icons provider, not null
     */
    public void setItemIconProvider(Function<T, Resource> itemIconProvider) {
        Objects.requireNonNull(itemIconProvider);
        this.itemIconProvider = itemIconProvider;
    }

    /**
     * Returns the item caption provider.
     *
     * @return the captions provider
     * @see #setItemCaptionProvider
     */
    public Function<T, String> getItemCaptionProvider() {
        return itemCaptionProvider;
    }

    /**
     * Sets the item caption provider for this radiobutton group. The caption
     * provider is queried for each item to optionally display an item textual
     * representation. The default provider returns
     * {@code String.valueOf(item)}.
     *
     * @param itemCaptionProvider
     *            the item caption provider, not null
     */
    public void setItemCaptionProvider(
            Function<T, String> itemCaptionProvider) {
        Objects.requireNonNull(itemCaptionProvider);
        this.itemCaptionProvider = itemCaptionProvider;
    }

    /**
     * Returns the item enabled predicate.
     *
     * @return the item enabled predicate
     * @see #setItemEnabledProvider
     */
    public Predicate<T> getItemEnabledProvider() {
        return itemEnabledProvider;
    }

    /**
     * Sets the item enabled predicate for this radiobutton group. The predicate is
     * applied to each item to determine whether the item should be enabled
     * (true) or disabled (false). Disabled items are displayed as grayed out
     * and the user cannot select them. The default predicate always returns
     * true (all the items are enabled).
     *
     * @param itemEnabledProvider
     *            the item enable predicate, not null
     */
    public void setItemEnabledProvider(Predicate<T> itemEnabledProvider) {
        Objects.requireNonNull(itemEnabledProvider);
        this.itemEnabledProvider = itemEnabledProvider;
    }
}
