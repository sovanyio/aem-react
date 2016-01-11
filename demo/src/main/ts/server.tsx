import ServerRenderer from "aem-react-js/ServerRenderer";
import RootComponentRegistry from "aem-react-js/RootComponentRegistry";
import componentRegistry from "./componentRegistry";
import {Container} from "aem-react-js/di/container";
import {createMemoryHistory} from "history";
import Cache from "aem-react-js/store/Cache";
import ResourceMappingImpl from "aem-react-js/router/ResourceMappingImpl";
import {StoreLocatorService} from "./storelocator/StoreLocatorService";



declare var Cqx: any;
declare var AemGlobal: any;
console.log("initializing AemGlobal");
//AemGlobal = {};

let rootComponentRegistry: RootComponentRegistry = new RootComponentRegistry();


rootComponentRegistry.add(componentRegistry);
rootComponentRegistry.init();

AemGlobal.registry = rootComponentRegistry;

AemGlobal.renderReactComponent = function (path: string, component: string, props: any): any {
    let container: Container = new Container();
    container.register("javaSling", Cqx.sling);
    let url: string = Cqx.sling.getUrl();
    let cache: Cache = new Cache();
    container.register("cache", cache);
    container.register("history", createMemoryHistory(url));
    container.register("resourceMapping", new ResourceMappingImpl(".html"))
    container.register("storeLocatorService", new StoreLocatorService(cache, container))

    let serverRenderer: ServerRenderer = new ServerRenderer(rootComponentRegistry, container);
    return serverRenderer.renderReactComponent(path, component, props);
}
