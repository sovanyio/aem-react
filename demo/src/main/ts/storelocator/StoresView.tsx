import * as React from "react";
import AemComponent from "aem-react-js/component/AemComponent";
import AemLink from "aem-react-js/router/AemLink";
import {Store, StoreLocatorService} from "./StoreLocatorService";
import {ResourceMapping} from "aem-react-js/router/ResourceMapping";


export default class StoresView extends AemComponent<any, any> {


    public render(): React.ReactElement<any> {
        let storeList: React.ReactElement<any>[] = this.renderStoreList();
        return (
            <div>
                <ul>
                    {storeList}
                </ul>
                <div className="detail">
                    {this.props.children}
                </div>
            </div>
        );
    }

    public renderStoreList(): React.ReactElement<any>[] {
        let storeList: React.ReactElement<any>[] = [];

        let resourceMapping: ResourceMapping = this.context.aemContext.container.get("resourceMapping");


        let service: StoreLocatorService = this.getAemContext().container.get("storeLocatorService");
        let stores: Store[] = service.findStores(this.props.route.baseResourcePath);

        let index: string = resourceMapping.map(this.props.route.baseResourcePath);

        storeList.push(<li key="home">
            <AemLink is to={index} x-cq-linkchecker="skip">Home</AemLink>
        </li>);

        stores.forEach(function (model: any, childIdx: number): void {
            let link: string = resourceMapping.map(this.props.route.baseResourcePath + "/" + model.id);
            storeList.push(<li key={model.id}>
                <AemLink is to={link} x-cq-linkchecker="skip">{model.name}</AemLink>
            </li>);
        }, this);
        return storeList;
    }

}


