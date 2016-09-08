/*
 * Copyright 2016, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.manager.client.assets.browser;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.openremote.manager.client.Environment;
import org.openremote.manager.client.assets.AssetMapper;
import org.openremote.manager.client.event.bus.EventBus;
import org.openremote.manager.client.event.bus.EventRegistration;
import org.openremote.manager.client.mvp.AppActivity;
import org.openremote.manager.client.util.TextUtil;
import org.openremote.manager.shared.Consumer;
import org.openremote.manager.shared.asset.Asset;
import org.openremote.manager.shared.asset.AssetResource;
import org.openremote.manager.shared.map.GeoJSON;
import org.openremote.manager.shared.map.GeoJSONFeature;
import org.openremote.manager.shared.map.GeoJSONGeometry;

import java.util.Collection;
import java.util.logging.Logger;

import static org.openremote.manager.client.http.RequestExceptionHandler.handleRequestException;

abstract public class AssetBrowsingActivity<V extends AssetBrowsingView, T extends AssetBrowsingPlace>
    extends AppActivity<T>
    implements AssetBrowsingView.Presenter {

    private static final Logger LOG = Logger.getLogger(AssetBrowsingActivity.class.getName());

    final protected Environment environment;
    final protected V view;
    final protected AssetBrowser.Presenter assetBrowserPresenter;
    final protected AssetResource assetResource;
    final protected AssetMapper assetMapper;

    protected EventRegistration<AssetSelectedEvent> assetSelectionRegistration;
    protected String assetId;
    protected Asset asset;

    public AssetBrowsingActivity(Environment environment,
                                 V view,
                                 AssetBrowser.Presenter assetBrowserPresenter,
                                 AssetResource assetResource,
                                 AssetMapper assetMapper) {
        this.environment = environment;
        this.view = view;
        this.assetBrowserPresenter = assetBrowserPresenter;
        this.assetResource = assetResource;
        this.assetMapper = assetMapper;
    }

    public V getView() {
        return view;
    }

    @Override
    protected String[] getRequiredRoles() {
        return new String[]{"read:assets"};
    }

    @Override
    protected AppActivity<T> init(T place) {
        this.assetId = place.getAssetId();
        return this;
    }


    @Override
    public void start(AcceptsOneWidget container, EventBus eventBus, Collection<EventRegistration> registrations) {
        //noinspection unchecked
        view.setPresenter(this);

        container.setWidget(view.asWidget());

        assetSelectionRegistration = assetBrowserPresenter.onSelection(
            event -> {
                if (event.getAssetId() == null) {
                    onAssetsDeselected();
                } else {
                    if (assetId == null || !assetId.equals(event.getAssetId())) {
                        onAssetSelectionChange(event.getAssetId());
                    }
                }

                // Put on global event bus so interested 3rd party can get state (e.g. header presenter)
                eventBus.dispatch(event);
            }
        );

        asset = null;
        if (assetId != null) {
            onBeforeAssetLoad();
            loadAsset(assetId, loadedAsset -> {
                this.asset = loadedAsset;
                assetBrowserPresenter.selectAsset(asset.getId(), asset.getPath());
                onAssetLoaded();
            });
        } else {
            startCreateAsset();
        }
    }

    public String getAssetId() {
        return assetId;
    }

    public Asset getAsset() {
        return asset;
    }

    @Override
    public void onStop() {
        if (assetSelectionRegistration != null) {
            assetBrowserPresenter.removeRegistration(assetSelectionRegistration);
        }
        super.onStop();
    }

    protected void loadAsset(String id, Consumer<Asset> assetConsumer) {
        environment.getRequestService().execute(
            assetMapper,
            requestParams -> assetResource.get(requestParams, id),
            200,
            assetConsumer::accept,
            ex -> handleRequestException(ex, environment)
        );
    }

    /**
     * Noop by default
     */
    protected void onBeforeAssetLoad() {
    }

    abstract protected void onAssetLoaded();

    abstract protected void onAssetsDeselected();

    abstract protected void onAssetSelectionChange(String selectedAssetId);

    protected void startCreateAsset() {
        assetBrowserPresenter.deselectAsset();
    }

    protected GeoJSON getFeature(Asset asset) {
        if (asset == null
            || asset.getId() == null
            || asset.getName() == null
            || asset.getCoordinates() == null)
            return GeoJSON.EMPTY_FEATURE_COLLECTION;

        return new GeoJSON().setType("FeatureCollection").setFeatures(
            new GeoJSONFeature().setType("Feature")
                .setProperty("id", asset.getId())
                .setProperty("title", TextUtil.ellipsize(asset.getName(), 20))
                .setGeometry(
                    new GeoJSONGeometry().setPoint(
                        asset.getCoordinates()
                    )
                )
        );
    }

}