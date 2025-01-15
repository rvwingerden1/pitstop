import {enableProdMode} from '@angular/core';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';

import {AppModule} from './app/app.module';
import {environment} from './environments/environment';

import localeSv from '@angular/common/locales/sv';
import localNl from '@angular/common/locales/extra/nl';
import {registerLocaleData} from "@angular/common";

if (environment.production) {
  enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));

registerLocaleData(localeSv, 'nl-NL', localNl)
