import {CommandGateway} from './command-gateway';
import {HandlerOptions} from "./handler";

export function HandleCommand(name?: string, options?: HandlerOptions): MethodDecorator {
  return (target: any, propertyKey: string, descriptor: PropertyDescriptor): PropertyDescriptor => {
    CommandGateway.registerHandlerInvoker(target.constructor.name, name || propertyKey, descriptor.value, options);
    return descriptor;
  }
}
