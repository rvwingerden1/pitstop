import {ReplaySubject, Subject, Subscription} from "rxjs";

export class LocalStorageValue<T> {
  private subject: Subject<T>;
  private key: string;
  private value: T;
  private isPrimitive: boolean;

  constructor(key: string, defaultValue: T = null) {
    this.key = key;
    this.subject = new ReplaySubject<T>(1);
    const val = localStorage.getItem(key);
    try {
      this.next((JSON.parse(val) as T) || defaultValue);
      this.isPrimitive = false;
    } catch(exception) {
      this.next((val as T) || defaultValue);
      this.isPrimitive = true;
    }
  }

  getValue(): T {
    return this.value;
  }

  subscribe(next?: ((value: T) => void) | null, error?: ((error: any) => void) | null, complete?: (() => void) | null): Subscription {
    return this.subject.subscribe({
      next: next,
      error: error,
      complete: complete
    });
  }

  next(value: T) {
    this.value = value;
    localStorage.setItem(this.key, this.isPrimitive ? value as string : JSON.stringify(value));
    this.subject.next(value);
  }
}
