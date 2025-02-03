import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { ChainedCurlComponent } from './app/view/chained-curl/chained-curl.component';
import { ChainedCurlListComponent } from './app/view/chained-curl-list/chained-curl-list.component';
import { ChainedCurlTabComponent } from './app/view/chained-curl-tab/chained-curl-tab.component';

bootstrapApplication(ChainedCurlTabComponent, appConfig)
  .catch((err) => console.error(err));
