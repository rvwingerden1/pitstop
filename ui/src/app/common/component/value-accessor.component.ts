import {ControlValueAccessor} from '@angular/forms';


export abstract class AbstractValueAccessorComponent<T> implements ControlValueAccessor {
  protected onTouchedCallback: () => void = (): void => { };
  protected onChangeCallback: (item: T) => void = (): void => { };

  abstract get value(): T;
  abstract writeValue(value: T): void;

  onModelChange() {
    this.onChangeCallback(this.value);
  }

  registerOnChange(onChangeFunction: (item: T) => void): void {
    this.onChangeCallback = onChangeFunction;
  }

  registerOnTouched(onTouchedFunction: () => void): void {
    this.onTouchedCallback = onTouchedFunction;
  }
}
