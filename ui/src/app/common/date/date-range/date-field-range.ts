import {Moment} from "moment";

export interface DateFieldRange {
  label?: string;
  start?: string;
  end?: string;
}

export interface MomentDateFieldRange {
  label: string;
  start: Moment;
  end: Moment;
}