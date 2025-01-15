import {ElementRef, Injectable} from "@angular/core";
import {HandlerRegistration, HandlerRegistry} from "./handler-registry.service";
import {Handler, HandlerInvoker, HandlerOptions} from './handler';
import {Observable, of} from 'rxjs';

@Injectable()
export abstract class Gateway {
  protected handlers: Map<string, Handler[]> = new Map();
  protected invokers: Map<string, HandlerInvoker[]> = new Map();

  protected constructor(invokers: Map<string, HandlerInvoker[]>, protected registry: HandlerRegistry) {
    this.invokers = invokers;
    registry.onRegistered.subscribe(registration => {
      registration.onDestroy.push(this.registerHandler(registration));
    });
  }

  protected tryHandleOnDom(type: string, payload: any, elementRef?: ElementRef<Element>): Observable<any> {
    if (elementRef) {
      const domEvent = new CustomEvent<any>(type, {
        detail: payload,
        bubbles: true,
        cancelable: true
      });
      elementRef.nativeElement.dispatchEvent(domEvent);
      if (domEvent.hasOwnProperty("$result")) {
        console.debug(`Locally handled on DOM: ${type}`);
        const result = domEvent['$result'];
        return result instanceof Observable ? result : of(result);
      }
    }
  }

  protected getLocalHandler(type: string): Handler {
    const handlers = this.handlers.get(type) || [];
    if (handlers.length > 1) {
      console.warn("Found multiple handlers for type, returning first handler", type, handlers);
    }
    return handlers[0];
  }

  private registerHandler(handler: HandlerRegistration): () => void {
    const targetInvokers = this.invokers.get(handler.targetClassName);
    if (!targetInvokers) {
      return () => {};
    }
    const elementFunctions: (() => void)[] = [];
    targetInvokers.forEach(i => {
      if (handler.elementRef && !i.options?.global) {
        const elementFunction = function (event: CustomEvent) {
          event['$result'] = i.method.apply(handler.instance, [event.detail, event]);
          switch (i.messageType) {
            case "query":
            case "command":
              event.stopPropagation();
              break;
          }
        };
        const nativeElement = handler.elementRef.nativeElement;
        nativeElement.addEventListener(i.type, elementFunction);
        elementFunctions.push(() => nativeElement.removeEventListener(i.type, elementFunction));
        console.debug(`Added DOM handler for ${i.type} (${handler.targetClassName}). Handler: `, handler.instance);
      } else {
        const handlers = this.handlers.get(i.type) || [];
        handlers.push({
          instance: handler.instance,
          invoker: i
        });
        this.handlers.set(i.type, handlers);
      }
    });
    return () => {
      console.debug(this.constructor.name, "Handler is being destroyed", handler.targetClassName, handler.instance);
      elementFunctions.forEach(f => f());
      this.handlers.forEach((handlers, eventName) => this.handlers
        .set(eventName, handlers.filter(h => h.instance !== handler.instance)));
    };
  }

  protected static addInvoker(invokers: Map<string, HandlerInvoker[]>, targetName: string, messageName: string, handler: Function,
                              messageType: 'event' | 'query' | 'command', options?: HandlerOptions) {
    const targetHandlers = invokers.get(targetName) || [];
    targetHandlers.push({
      targetClassName: targetName,
      type: messageName,
      method: handler,
      messageType : messageType,
      options: options
    });
    console.debug(`Adding handler ${messageName}(${messageType}) to targetHandlers`, targetName, targetHandlers);
    invokers.set(targetName, targetHandlers);
  }

  protected getPayload(message: any) {
    return message && message['@class'] === 'io.fluxcapacitor.javaclient.common.Message' ? message.payload : message;
  }
}
