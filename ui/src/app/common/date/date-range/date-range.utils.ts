import {DatePickerRange} from "../date-picker/date-picker.component";
import {MomentDateFieldRange} from "./date-field-range";

export class DateRangeUtils {

    static getRangeLabel = (value: DatePickerRange, ranges: MomentDateFieldRange[]): string => {
        return (ranges || []).find(a => this.rangeSelected(value, a))?.label;
    }

    static rangeSelected = (value: DatePickerRange, range: MomentDateFieldRange): boolean => {
        return value.start.isSame(range.start, 'day') && value.end.isSame(range.end, 'day');
    }
}