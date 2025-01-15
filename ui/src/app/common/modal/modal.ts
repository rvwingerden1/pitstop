import Options = Modal.Options;
import Modal from 'bootstrap/js/dist/modal';


export enum AnimationTypes {
  fade = "fade",
  zoomIn = "zoom-in"
}

export const defaultModalOptions : ModalOptions = { cssClass: "modal-lg", animation: AnimationTypes.fade, closeOnSuccess: true, backdrop: true };

export interface ModalOptions extends Partial<Options> {
  closeOnSuccess?: boolean;
  cssClass?: string;
  style?: string;
  animation?: AnimationTypes;
  closeCallback?: (args?: any) => any;
}

export interface OpenModal {
  component;
  container?;
  data?;
  options?: ModalOptions;
  initializer?: <T> (v : T) => void
}
