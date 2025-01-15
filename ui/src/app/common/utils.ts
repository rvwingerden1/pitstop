import _ from 'lodash';
import {v4 as uuidv4} from 'uuid';
import {from, Observable} from 'rxjs';
import {ElementRef} from "@angular/core";
import {TitleCasePipe} from "@angular/common";
import {AppCommonUtils, InjectorProvider} from "./app-common-utils";
import {ComparatorChain} from './comparator-chain';
import {isSafeNumber, parse} from 'lossless-json';

export const lodash = _;

export const localTimeFormat = 'YYYY-MM-DDTHH:mm:ss';

export function sort<T>(values : T[], comparatorChain : ComparatorChain) : T[] {
  return lodash.assign([], values).sort(comparatorChain.compare);
}


export function parseNumberIfSafe(value: string) {
  return isSafeNumber(value) ? parseFloat(value) : value;
}

export function parseJson(json: string): unknown {
  return parse(json, null, parseNumberIfSafe);
}

export function cloneObject<T, V extends T>(obj: T): V {
    return <V>lodash.cloneDeep(obj);
}

export function hasChanged(newData: any, existingData: any) {
  return !lodash.isMatch(omitNilDeep(existingData), omitNilDeep(newData));

  function omitNilDeep(object: any) : any {
    return lodash.transform(object, (r, v, k) => {
      if (lodash.isNil(v)) return;
      r[k] = lodash.isObject(v) ? omitNilDeep(v) : v;
    });
  }
}

export function extractValue<T>(option: T, key: string): any {
    let result = option;
    if (key) {
        const splitKey = key.split('.');
        splitKey.forEach(k => result = result && result[k]);
    }
    return result;
}

export function newObjectFromValue(value: any, path: string): any {
    if (!path) {
        return value || null;
    }
    if (!value) {
        return null;
    }
    const result = {};
    let last = result;
    const parts = path.split(".");
    const length = parts.length;
    for (let i = 0; i < length; i++) {
        const p = parts[i];
        if (i + 1 < length) {
            last = last[p] = {};
        } else {
            last[p] = value;
        }
    }
    return result;
}

export function removeIf<T>(array: T[], callbackfn: (value: T, index: number, array: T[]) => boolean): boolean {
    let i = array.length;
    let removed = false;
    while (i--) {
        if (callbackfn(array[i], i, array)) {
            array.splice(i, 1);
            removed = true;
        }
    }
    return removed;
}

export function removeItem<T>(array: T[], value: T): T[] {
    removeIf(array, v => v === value);
    return array;
}

export function removeAll<T>(array: T[], toRemove: T[]): T[] {
    removeIf(array, v => toRemove.indexOf(v) >= 0);
    return array;
}

export function replaceItem<T>(array: T[], oldValue: T, newValue: T): T[] {
    if (oldValue) {
        if (newValue) {
            array.splice(array.indexOf(oldValue), 1, newValue);
        } else {
            removeItem(array, oldValue);
        }
    } else if (newValue) {
        array.push(newValue);
    }
    return array;
}

export function computeItem<T>(array: T[], predicate : (t : T) => boolean , mapper: (t : T) => T): T[] {
  const oldValue = array.find(predicate);
  const newValue = mapper(oldValue);
  return replaceItem(array, oldValue, newValue);
}

const isEmpty = (value?: any) => value == null || (Array.isArray(value) && !value.length)
    || (typeof value === "object" && Object.keys(value).length == 0);

export function uuid(): string {
    return replaceAll(uuidv4(), '-', '');
}

function escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
}

export function replaceAll(str, find, replace) {
    return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

export function dispatchChangeEvent(element: HTMLElement) {
    let event;
    if (typeof (Event) === 'function') {
        event = new Event('change', {bubbles: true, cancelable: true});
    } else {
        event = document.createEvent("HTMLEvents");
        event.initEvent("change", true, true);
    }
    element.dispatchEvent(event);
    element.dispatchEvent(event);
}

export function filterValuesByTerm(values, term, excludedFilterFields: string[] = []) {
    const filtered = values.filter(v => filterByTerm(term, excludedFilterFields)(v));
    return filtered.slice(0, Math.min(filtered.length, 20));
}

export function filterByTerm(filterTerm: string, excludedFilterFields: string[] = []): (item) => boolean {
    return filterTerm
        ? filterByTermArray(filterTerm.split(' '), excludedFilterFields)
        : () => true;
}

export function filterByTermArray(filterTerms: string[], excludedFilterFields: string[] = []): (item) => boolean {
    return item => {
        let jsonValues = '';
        let cache = [];
        JSON.stringify(item, (key, value) => {
            if (cache.includes(value)) {
                return;
            }
            cache.push(value);
            if (excludedFilterFields.indexOf(key) == -1) {
                jsonValues += value + ' ';
                return value;
            }
            return undefined;
        });
        cache = null;
        jsonValues = jsonValues.toLowerCase();
        return filterTerms.filter(t => !!t).some(term => jsonValues.indexOf(term.toLowerCase()) >= 0);
    };
}

export function checkValidity(e: ElementRef | HTMLElement): boolean {
    const element: HTMLElement = e['nativeElement'] || e;
    if (element.querySelector('.ng-invalid')) {
        element.classList.add('was-validated');
        const alert = AppCommonUtils.registerError('Please review the fields with errors.');
        const jElement = $(element);
        const handler = () => {
          AppCommonUtils.closeAlerts(alert);
            jElement.off('change', handler);
        };
        jElement.on('change', handler);
        return false;
    }
    element.classList.remove('was-validated');
    return true;
}

export function copyToClipboard(text: string, showSuccess: boolean = false): Observable<void> {
  const observable = from(navigator.clipboard.writeText(text));
  if (showSuccess) {
    observable.subscribe(() => AppCommonUtils.registerSuccess("Value copied to clipboard!", 2000));
  }
  return observable;
}

export function scrollToTop() {
    $('html, body').animate({scrollTop: 0}, 'fast');
}

/**
 * Returns a bezier interpolated value, using the given ranges
 * @param {number} value  Value to be interpolated
 * @param {number} s1 Source range start
 * @param {number} s2  Source range end
 * @param {number} t1  Target range start
 * @param {number} t2  Target range end
 * @param {number} [slope]  Weight of the curve (0.5 = linear, 0.1 = weighted near target start, 0.9 = weighted near target end)
 * @returns {number} Interpolated value
 */
export function interpolate(value, s1, s2, t1 = 0, t2 = 1, slope = 0.5) {
    //Default to linear interpolation
    slope = slope || 0.5;

    //If the value is out of the source range, floor to min/max target values
    if(value < Math.min(s1, s2)) {
        return Math.min(s1, s2) === s1 ? t1 : t2;
    }

    if(value > Math.max(s1, s2)) {
        return Math.max(s1, s2) === s1 ? t1 : t2;
    }

    //Reverse the value, to make it correspond to the target range (this is a side-effect of the bezier calculation)
    value = s2-value;

    var C1 = {x: s1, y:t1}; //Start of bezier curve
    var C3 = {x: s2, y:t2}; //End of bezier curve
    var C2 = {              //Control point
        x: C3.x,
        y: C1.y + Math.abs(slope) * (C3.y - C1.y)
    };

    //Find out how far the value is on the curve
    var percent = value / (C3.x-C1.x);
    const b1 = (t) => t * t;
    const b2 = (t) => 2 * t * (1 - t);
    const b3 = (t) => (1 - t) * (1 - t);

    return C1.y*b1(percent) + C2.y*b2(percent) + C3.y*b3(percent);
}

export function toTitleCase(value: string): string {
    if (!titleCasePipe) {
        titleCasePipe = InjectorProvider.injector.get(TitleCasePipe);
    }
    return value ? titleCasePipe.transform(value) : '';
}

let titleCasePipe;

export function trackByIndex(i: number, rec: any) {
    return i;
}
