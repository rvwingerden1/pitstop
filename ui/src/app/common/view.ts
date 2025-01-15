import {ElementRef, inject, ViewContainerRef} from '@angular/core';
import {RequestOptions} from './request-gateway';
import {Observable, Subject} from 'rxjs';
import {QueryGateway} from './query-gateway';
import {CommandGateway, CommandOptions} from "./command-gateway";
import {defaultModalOptions, ModalOptions, OpenModal} from './modal/modal';
import {sendCommand} from './app-common-utils';
import {lodash} from './utils';


export class View {
  private queryGateway = inject(QueryGateway);
  private commandGateway = inject(CommandGateway);
  elementRef: ElementRef = inject(ElementRef);
  viewContainerRef: ViewContainerRef = inject(ViewContainerRef);
  subjects: Subject<any>[] = [];

  subscribeTo(type: string, payload = {}, options: RequestOptions = undefined): Observable<any> {
    const subject = this.queryGateway.subscribe(type, payload, options, this.elementRef);
    this.subjects.push(subject);
    return subject;
  }

  sendQuery(type: string, payload = {}, options?: RequestOptions): Observable<any> {
    return this.queryGateway.send(type, payload, options, this.elementRef);
  }

  sendCommand(type: string, payload, successHandler?: (value: any) => void,
              errorHandler?: (error: any) => void, options: CommandOptions = {eventOnSuccess: true}): void {
    sendCommand(type, payload, successHandler, errorHandler, options, this.elementRef);
  }

  sendCommandAndForget(type: string, payload = {}, options?: RequestOptions): void {
    this.commandGateway.sendAndForget(type, payload, options, this.elementRef);
  }

  openModal = <T> (component: new (...args: any[]) => T, initializer?: (v : T) => void, options: ModalOptions = defaultModalOptions, closeCallback?: (args?: any) => any) => {
    options = lodash.merge({}, defaultModalOptions, options);
    options.closeCallback = closeCallback;
    this.commandGateway.send("openModal", <OpenModal>{
      component: component,
      container: this.viewContainerRef,
      options: options,
      initializer: initializer
    });
  };

  // Angular doesn't support Custom Decorators on Components. For this reason we cannot declare ngOnDestroy here because it
  // will break all components. See: https://github.com/angular/angular/issues/48276#issuecomment-1332611012
  readonly onDestroy = () => {
    this.subjects.forEach(s => s.complete());
  }
}
