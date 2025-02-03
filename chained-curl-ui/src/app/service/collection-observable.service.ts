import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ObserverValue {
  observe(data: string): void;
}

@Injectable({
  providedIn: 'root'
})
export class CollectionObservableService {

  observersMap: Map<string, BehaviorSubject<string>[]> = new Map();

  constructor() { }

  public observe(contextId: string, key: string, observable: BehaviorSubject<string>) {
    if (!this.observersMap.has(contextId + '-' + key)) {
      this.observersMap.set(contextId + '-' + key, []);
    }
    this.observersMap.get(contextId + '-' + key).push(observable);
  }

  setValue(contextId: string, key: string, data: string) {
    if (this.observersMap.has(contextId + '-' + key)) {
      this.observersMap.get(contextId + '-' + key).forEach(value => {
        value.next(data);
      });
    }
  }
}
