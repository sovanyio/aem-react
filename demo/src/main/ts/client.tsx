import ComponentManager from "aem-react-js/ComponentManager";
import {Container} from "aem-react-js/di/Container";
import RootComponentRegistry from "aem-react-js/RootComponentRegistry";
import ClientSling from "aem-react-js/store/ClientSling";
import Cache from "aem-react-js/store/Cache";
import componentRegistry from "./componentRegistry";
import {createHistory} from "history";
import ResourceMappingImpl from "aem-react-js/router/ResourceMappingImpl";
import {StoreLocatorService} from "./storelocator/StoreLocatorService";

let rootComponentRegistry: RootComponentRegistry = new RootComponentRegistry();
rootComponentRegistry.add(componentRegistry);
rootComponentRegistry.init();

let container: Container = new Container();
let cache: Cache = new Cache();
let clientSling: ClientSling = new ClientSling(cache, "http://localhost:4502");
container.register("sling", clientSling);
container.register("cache", cache);
container.register("history", createHistory());
container.register("resourceMapping", new ResourceMappingImpl(".html"))
container.register("storeLocatorService", new StoreLocatorService(cache, container))
let componentManager: ComponentManager = new ComponentManager(rootComponentRegistry, container);

componentManager.initReactComponents();

interface MyWindow { 
    AemGlobal: any;
}
declare var window: MyWindow;

if (typeof window === "undefined") {
    throw "this is not the browser";
}

window.AemGlobal = {componentManager: componentManager};
