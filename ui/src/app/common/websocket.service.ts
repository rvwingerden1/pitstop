import {environment} from "../../environments/environment";
import {Injectable, OnDestroy} from "@angular/core";
import {AppCommonUtils} from "./app-common-utils";

@Injectable()
export class WebsocketService<T> implements OnDestroy {
  private socket: WebSocket;

  private endpoint: string;
  private onMessage: (update: T) => void;
  private retryOnClose: boolean = false;
  private onClose?: (closeEvent: CloseEvent) => void;

  initialise = (endpoint: string, onMessage: (update: T) => void,
                retryOnClose: boolean = true, onClose?: (closeEvent: CloseEvent) => void) => {
    this.endpoint = endpoint;
    this.onMessage = onMessage;
    this.retryOnClose = retryOnClose;
    this.onClose = onClose;
    this.openWebsocket();
  }

  ngOnDestroy() {
    try {
      console.debug("Closing websocket");
      this.socket.onclose = () => {};
      this.socket.close();
      this.socket = null;
    } catch (ignored) {
    }
  }

  private openWebsocket = () => {
    try {
      if (!localStorage.getItem("Authorization")) {
        return; //signed out
      }
      const url = this.getUrl();
      this.socket = new WebSocket(url, getProtocolFromHeaders("Authorization", "X-Impersonation"));
      this.subscribeToSocket();
    } catch (e) {
      console.warn(`Could not open websocket (${this.endpoint}). Retrying every minute...`, e);
      setTimeout(() => this.openWebsocket(), 60_000);
      return;
    }

    function getProtocolFromHeaders(...headerNames: string[]) : string[] {
      const result : string[] = [];
      headerNames.forEach(headerName => {
        const value = localStorage.getItem(headerName);
        if (value) {
          result.push(encodeURIComponent(headerName), encodeURIComponent(value));
        }
      });
      return result;
    }
  }

  private getUrl = () => environment.apiProtocol + environment.apiDomain + '/' + this.endpoint;

  private subscribeToSocket = () => {
    this.socket.onmessage = this.onMessageReceived;
    this.socket.onclose = this.onSocketClose;
    this.socket.onopen = this.onSocketOpen;
  };

  private onMessageReceived = (message: MessageEvent) => {
    if (typeof message.data === 'string') {
      try {
        const update: T = JSON.parse(message.data);
        if (update) {
          if (!this.onMessage) {
            this.ngOnDestroy();
            return;
          }
          this.onMessage(update);
        }
      } catch (exception) {
        console.error("Error while parsing message", exception);
        AppCommonUtils.registerError("Error while receiving automated message from backend, please contact support");
      }
    }
  }

  private onSocketClose = (event: CloseEvent) => {
    if (!event.wasClean) {
      console.warn(`Websocket closed with reason: ${event.reason} (${event.code}).${this.retryOnClose ? ' Trying to reconnect...' : ''}`);
    } else {
      console.debug(`Websocket closed with reason: ${event.reason} (${event.code}).${this.retryOnClose ? ' Trying to reconnect...' : ''}`);
    }
    if (this.retryOnClose) {
      setTimeout(() => this.openWebsocket(), 5_000);
    } else if (this.onClose) {
      this.onClose(event);
    }
  }

  private onSocketOpen = () => {
    console.debug(`Websocket opened (url: ${this.getUrl()})`);
  }
}
