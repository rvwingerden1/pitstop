import {QueryGateway} from "./query-gateway";
import {HandlerOptions} from "./handler";

export function HandleQuery(name?: string, options?: HandlerOptions): MethodDecorator {
  return (target: any, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor => {
    QueryGateway.registerHandlerInvoker(target.constructor.name, name || propertyKey, descriptor.value, options);
    return descriptor;
  }
}
