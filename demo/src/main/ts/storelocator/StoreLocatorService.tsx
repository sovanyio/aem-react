import Cache from "aem-react-js/store/Cache";
import {Container} from "aem-react-js/di/Container";

export interface Store {
    name: string;
    id: string;
}

export class StoreLocatorService {
    constructor(cache: Cache, container: Container) {
        this.cache = cache;
        this.container = container;
    }

    private cache: Cache;
    private container: Container;

    public findStores(basePath: string): Store[] {
        let cacheKey: string = this.cache.generateServiceCacheKey("StoreLocatorService", "findStores", arguments);
        return this.cache.wrapServiceCall(cacheKey, (): Store[] => {
            let service: any = this.container.getOsgiService("com.sinnerschrader.aem.react.demo.StoreLocatorService");
            let result: string =  service.findStores(basePath);
            return JSON.parse(result);
        });
    }
}
