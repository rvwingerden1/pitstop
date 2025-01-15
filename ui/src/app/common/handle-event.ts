import {EventGateway} from "./event-gateway";

export function HandleEvent(name?: string): MethodDecorator {
  return (target: any, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor => {
    EventGateway.registerHandlerInvoker(target.constructor.name, name || propertyKey, descriptor.value);
    return descriptor;
  }
}
