import moment from 'moment';
import {TimestampPipe} from './timestamp.pipe';

import {InjectorProvider} from '../app-common-utils';

export function roundToQuarterHour(timestamp) {
  if (timestamp) {
    const roundedTime = moment(timestamp);
    roundedTime.set({minutes: Math.round(roundedTime.minutes() / 15) * 15});
    return roundedTime.toISOString();
  }
  return timestamp;
}

export function formatTimestamp(value: any, format?) {
  if (!timestampPipe) {
    timestampPipe = InjectorProvider.injector.get(TimestampPipe);
  }
  return value ? timestampPipe.transform(value, format) : '';
}

let timestampPipe;
