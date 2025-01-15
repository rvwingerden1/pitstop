import {inject, Injectable} from "@angular/core";
import {StandaloneHandler} from "../common/standalone-handler";
import {HandleQuery} from "../common/handle-query";
import {AppCommonUtils} from "../common/app-common-utils";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {filter, Observable} from "rxjs";
import {ActivatedFluxRoute} from "../app-routing.module";

@Injectable({
  providedIn: "root"
})
export class RouterHandler extends StandaloneHandler {
  breadcrumbs: BreadcrumbInfo[] = [];
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);

  constructor() {
    super();
    this.router.events.pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => {
        this.breadcrumbs = this.createBreadcrumbs(this.activatedRoute.root);
        AppCommonUtils.modifyQueryCache("getBreadcrumbs", () => this.breadcrumbs);
      });
  }

  @HandleQuery("getBreadcrumbs", { caching: true })
  getBreadcrumbs(): BreadcrumbInfo[] {
    return this.breadcrumbs;
  }

  private createBreadcrumbs(route: ActivatedFluxRoute, url: string = '', breadcrumbs: BreadcrumbInfo[] = []): BreadcrumbInfo[] {
    const children: ActivatedFluxRoute[] = route.children;
    if (!children.length) {
      return breadcrumbs;
    }
    return children.reduce((acc, child) => {
      const routeURL: string = child.snapshot.url.map(segment => segment.path).join('/');
      const newUrl = routeURL === '' ? url : `${url}/${routeURL}`;
      if (child.snapshot.data.breadcrumbLabel) {
        acc.push({
          label: child.snapshot.data.breadcrumbLabel(child.snapshot.params),
          url: newUrl
        });
      }
      return this.createBreadcrumbs(child, newUrl, acc);
    }, breadcrumbs);
  }
}

export interface BreadcrumbInfo {
  label: Observable<string>;
  url: string;
}
