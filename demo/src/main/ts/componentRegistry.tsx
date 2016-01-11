import ComponentRegistry from "aem-react-js/ComponentRegistry";
import Embedded from "./embedded/embedded";
import Text from "./text/text";
import ReactParsys from "aem-react-js/component/ReactParsys";
import Accordion from "./accordion/accordion";
import AccordionElement from "./accordion/accordion-element";
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import ReactParsys from "aem-react-js/component/ReactParsys";
=======
import StoreLocatorApp from "./storelocator/StoreLocatorApp";
>>>>>>> 8a1413e...  more
=======
>>>>>>> 5bd2842... removed redux
=======
import StoreLocator from "./storelocator/StoreLocator";
import StoreView from "./storelocator/StoreView";
>>>>>>> 9e9c974... router is working

let registry: ComponentRegistry = new ComponentRegistry("react-demo/components");
registry.register(Embedded);
registry.register(Text);
registry.register(ReactParsys);
registry.register(Accordion);
registry.register(AccordionElement);
<<<<<<< HEAD
registry.register(ReactParsys);

=======
registry.register(StoreLocator);
registry.register(StoreView);
registry.register(AccordionElement);
>>>>>>> 9e9c974... router is working
export default registry;
