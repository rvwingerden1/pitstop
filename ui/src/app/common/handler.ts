import {ElementRef, OnDestroy, OnInit} from '@angular/core';
import {HandlerRegistry} from "./handler-registry.service";
import {InjectorProvider} from './app-common-utils';
import {View} from './view';

export function Handler() {
  return function <T extends { new(...args: any[]): {} }>(constructor: T) {
    return class extends constructor implements OnInit, OnDestroy {
      private cancelRegistration: () => void = () => {};

      ngOnInit(): void {
        super["ngOnInit"] ? super["ngOnInit"]() : null;
        const elementRef : ElementRef = this instanceof View ? (<View> this).elementRef : (this["elementRef"] || null);
        this.cancelRegistration = InjectorProvider.injector.get(HandlerRegistry).registerHandlerInstance(
          Object.getPrototypeOf(Object.getPrototypeOf(this)).constructor.name, this, elementRef);
      }

      ngOnDestroy(): void {
        super["ngOnDestroy"] ? super["ngOnDestroy"]() : null;
        this.cancelRegistration();
      }
    }
  }
}

export interface Handler {
  instance: any;
  invoker: HandlerInvoker;
}

export interface HandlerInvoker {
  targetClassName: any;
  type: string;
  method: Function;
  messageType: 'event' | 'query' | 'command';
  options: HandlerOptions;
}

export interface HandlerOptions {
  caching?: boolean;
  global?: boolean;
}
