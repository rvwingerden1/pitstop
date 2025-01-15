import {inject, Injectable} from '@angular/core';
import {AuthConfig, OAuthService} from 'angular-oauth2-oidc';
import {BehaviorSubject, from, lastValueFrom, Observable} from 'rxjs';

import {StatehandlerService} from './statehandler.service';
import {StandaloneHandler} from "../common/standalone-handler";
import {StorageService} from "./storage.service";
import {HandleQuery} from '../common/handle-query';
import {HandleCommand} from '../common/handle-command';

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService extends StandaloneHandler {
  private _authenticated: boolean = false;
  private readonly _authenticationChanged: BehaviorSubject<boolean> = new BehaviorSubject(this.authenticated);
  private oauthService: OAuthService = inject(OAuthService);
  private authConfig: AuthConfig = inject(AuthConfig);
  private statehandler: StatehandlerService = inject(StatehandlerService);
  private authStorage: StorageService = inject(StorageService);

  constructor() {
    super();
    this._authenticationChanged.subscribe(authenticated => this.updateCredentials(authenticated));
  }

  public get authenticated(): boolean {
    return this._authenticated;
  }

  public get authenticationChanged(): Observable<boolean> {
    return this._authenticationChanged;
  }

  public getOIDCUser(): Observable<any> {
    return from(this.oauthService.loadUserProfile());
  }

  public authenticate = async (setState: boolean = true): Promise<boolean> => {
    this.oauthService.configure(this.authConfig);
    this.oauthService.setupAutomaticSilentRefresh();

    this.oauthService.strictDiscoveryDocumentValidation = false;
    await this.oauthService.loadDiscoveryDocumentAndTryLogin();

    this._authenticated = this.oauthService.hasValidAccessToken();

    if (!this.oauthService.hasValidIdToken() || !this.authenticated) {
      const newState = setState ? await lastValueFrom(this.statehandler.createState()) : undefined;
      this.oauthService.initCodeFlow(newState);
    }
    this._authenticationChanged.next(this.authenticated);

    return this.authenticated;
  };

  public signout(): void {
    this.oauthService.logOut();
    this._authenticated = false;
    this._authenticationChanged.next(false);
  }

  @HandleQuery()
  getImpersonatedUser() : string {
    return localStorage.getItem("X-Impersonation");
  }

  @HandleCommand()
  impersonateUser(userId: string) {
    localStorage.setItem("X-Impersonation", userId);
  }

  @HandleCommand()
  stopImpersonating() {
    localStorage.removeItem("X-Impersonation");
  }

  private updateCredentials(authenticated: boolean) {
    const token = this.authStorage.getItem("id_token");
    if (authenticated && token) {
      localStorage.setItem("Authorization", "Bearer " + token);
    } else {
      localStorage.removeItem("Authorization");
    }
  }
}
