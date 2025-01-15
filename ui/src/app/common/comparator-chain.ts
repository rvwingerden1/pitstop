import _ from 'lodash';
import {lodash} from './utils';

const ASCENDING = "+";
const DESCENDING = "-";
const EQUAL = 0;
const BEFORE = -1;
const AFTER = 1;

export class ComparatorChain {
  private readonly unparsedProperties;
  private readonly properties;

  constructor(...properties) {
    this.unparsedProperties = Array.from(properties);
    this.properties = this.unparsedProperties
      .map(property => ComparatorChain.parseProperty(property));
  }

  static parseProperty = property => {
    if (_.isFunction(property)) {
      return property;
    }

    let direction = property.charAt(0);
    let hasDirection = direction === ASCENDING || direction === DESCENDING;
    let nullsFirst = true;
    if (hasDirection) {
      property = property.slice(1);
      nullsFirst = false;
    } else {
      direction = property.charAt(property.length - 1);
      hasDirection = direction === ASCENDING || direction === DESCENDING;
      if (hasDirection) {
        property = property.slice(0, property.length - 1);
      }
    }

    const existsCheck = property.startsWith('!!');
    property = existsCheck ? property.slice(2) : property;

    return {
      propertyName: property,
      direction: hasDirection ? direction : ASCENDING,
      nullsFirst: nullsFirst,
      existsCheck: existsCheck
    };
  };

  reverseProperties() : ComparatorChain {
    return new ComparatorChain(...lodash.clone(this.unparsedProperties).reverse())
  }

  compare = (a, b) : number => {
    let result = EQUAL;

    for (const property of this.properties) {
      if (_.isFunction(property)) {
        result = property.call(this, a, b);
      } else {
        result = ComparatorChain.compareProperty(a, b, property);
      }

      if (result !== EQUAL) {
        break;
      }
    }
    return result;
  };

  reverseCompare = (a, b) : number => {
    const f = this.compare;
    return f(b, a);
  }

  static compareProperty = (a, b, { propertyName, direction, nullsFirst, existsCheck }) => {
    const propertyA = getPropertyAtPath(a, propertyName, existsCheck);
    const propertyB = getPropertyAtPath(b, propertyName, existsCheck);

    if (propertyA === propertyB) {
      return EQUAL;
    }

    if (propertyA === null || propertyA === undefined) {
      return nullsFirst ? BEFORE : AFTER;
    }

    if (propertyB === null || propertyB === undefined) {
      return nullsFirst ? AFTER : BEFORE;
    }

    if (direction === ASCENDING) {
      return propertyA > propertyB ? AFTER : BEFORE;
    }
    return propertyA < propertyB ? AFTER : BEFORE;
  };
}

function getPropertyAtPath(object, path, existsCheck: boolean) {
  if (object === undefined || object === null || typeof path !== 'string') {
    return undefined;
  }

  const index = path.trim().indexOf('.');
  return index < 0
    ? existsCheck ? !!object[path] : object[path]
    : getPropertyAtPath(object[path.substr(0, index)], path.substr(index + 1), existsCheck);
}
