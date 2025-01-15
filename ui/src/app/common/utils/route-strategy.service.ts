import {ActivatedRouteSnapshot, BaseRouteReuseStrategy} from "@angular/router";
import lodash from "lodash";

export class RouteStrategyService extends BaseRouteReuseStrategy {

    shouldReuseRoute(future: ActivatedRouteSnapshot, current: ActivatedRouteSnapshot): boolean {
        if (!lodash.isUndefined(future.routeConfig?.data?.reUseRoute)) {
            return future.routeConfig.data.reUseRoute;
        }
        return super.shouldReuseRoute(future, current);
    }
}
