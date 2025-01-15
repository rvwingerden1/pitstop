import {AppCommonUtils} from "./common/app-common-utils";
import {environment} from '../environments/environment';
import {Role, UserProfile} from '@pitstop/typescriptmodels/pitstop';

export class AppContext {
  static userProfile: UserProfile;
  static initials: string;

  static setUserProfile = (userProfile: UserProfile) => {
    if (!userProfile) {
      AppCommonUtils.clearCache();
    }
    this.userProfile = userProfile;
    this.initials = userProfile && (userProfile.details.firstName + ' ' + userProfile.details.lastName)
      .match(/(\b\S)?/g).join("").match(/(^\S|\S$)?/g).join("").toUpperCase();
  }

  static isAdmin = () => Role.admin === this.userProfile.userRole;

  static isProduction = () => environment.production;
}
