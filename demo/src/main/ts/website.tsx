import * as React from "react";
import * as component from "aem-react-js/ComponentManager";
import Text from "./text/text";
import Accordion from "./accordion/accordion";
import StoreLocatorApp from "./storelocator/StoreLocatorApp";
import container from "./di/container";
import {createHistory} from "history";
import "babel-polyfill";
import ToActionQueue from "./di/ToActionQueue";

let queue: ToActionQueue = new ToActionQueue();
container.register("ActionQueue", queue);




container.register("history", createHistory());
component.ComponentManager.init({server: false});
let componentManager = component.ComponentManager.INSTANCE;

const comps: { [name: string]: typeof React.Component } = {
    // insert your react component classes here!
    Text, Accordion, StoreLocatorApp
};
componentManager.setComponents(comps);

