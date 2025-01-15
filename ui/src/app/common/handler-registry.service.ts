import {ElementRef, Injectable} from "@angular/core";
import {ReplaySubject} from "rxjs";
import {View} from "./view";

@Injectable()
export class HandlerRegistry {
  private components: Map<any, HandlerRegistration> = new Map();
  onRegistered: ReplaySubject<HandlerRegistration> = new ReplaySubject<HandlerRegistration>();

  registerHandlerInstance(targetName: string, instance: any, elementRef?: ElementRef): () => void {
    const registration: HandlerRegistration = {
      instance: instance,
      targetClassName: targetName,
      elementRef: elementRef,
      onDestroy: []
    }
    this.components.set(instance, registration);
    console.debug("Handler is being registered", registration);
    this.onRegistered.next(registration);
    return () => {
      const registration = this.components.get(instance);
      console.debug("Handler is being destroyed", registration);
      registration?.onDestroy.forEach(f => f());
      instance["onDestroy"] ? instance.onDestroy() : null;
      this.components.delete(instance);
    }
  }
}

export interface HandlerRegistration {
  instance: any;
  targetClassName: string;
  elementRef: ElementRef<Element>;
  onDestroy: Function[];
}
