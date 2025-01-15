import {InjectorProvider} from "./app-common-utils";
import {HandlerRegistry} from "./handler-registry.service";
import {Handler} from "./handler";

@Handler()
export class StandaloneHandler {

  constructor() {
    this["subscription"] = InjectorProvider.injector.get(HandlerRegistry).registerHandlerInstance(
      this.constructor.name, this);
  }
}
