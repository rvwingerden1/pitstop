import {
  ConfirmationModalComponent,
  ConfirmationModalPayload
} from './common/confirmation-modal/confirmation-modal.component';
import {sendCommandAndForget} from "./common/app-common-utils";

export function openConfirmationModal(component, data?) {
  sendCommandAndForget('openConfirmationModal', <ConfirmationModalPayload>{component: component, data: data});
}

export function openConfirmationModalWithCallback(callback: (confirmed : boolean) => any, component?, data?) {
  sendCommandAndForget('openConfirmationModal', <ConfirmationModalPayload>{
    component: component || ConfirmationModalComponent,
    data: data,
    callback: callback
  });
}

export function closeConfirmationModal() {
  sendCommandAndForget('closeConfirmationModal');
}
