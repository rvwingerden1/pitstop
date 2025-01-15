import {NgModule} from '@angular/core';
import {ActivatedRoute, ActivatedRouteSnapshot, Route, RouterModule} from '@angular/router';
import {HomeComponent} from "./views/home/home.component";
import {Observable} from "rxjs";
import {subscribeTo} from "./common/app-common-utils";
import {map} from "rxjs/operators";
import {EditProfileComponent} from "./views/user/edit-profile.component";
import {EditProfileGeneralComponent} from "./views/user/edit-profile-general/edit-profile-general.component";
import {AuthGuard} from "./authentication/auth.guard";
import {IncidentOverviewComponent} from './views/incident-overview/incident-overview.component';
import {RegisterOperatorComponent} from './views/user/register-operator/register-operator.component';

const routes: FluxHostRoute[] = [
  {
    title: "PitStop",
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
    children: [
      {
        title: "Incidents",
        path: '',
        component: IncidentOverviewComponent,
      }
    ]
  },
  {
    path: 'auth/callback',
    redirectTo: ''
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

export interface FluxHostRoute extends Route {
  data?: RouteData;
  children?: FluxHostRoute[];
}

export interface ActivatedFluxRoute extends ActivatedRoute {
  data: Observable<RouteData>;
  snapshot: ActivatedFluxRouteSnapshot;
}

export interface ActivatedFluxRouteSnapshot extends ActivatedRouteSnapshot {
  data: RouteData;
}

export interface RouteData {
  breadcrumbLabel?: (routeParams: any) => Observable<string>;
  activeTabIndex?: number;
}
