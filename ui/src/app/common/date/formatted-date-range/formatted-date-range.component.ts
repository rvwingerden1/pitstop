import {Component, Input} from '@angular/core';
import moment, {Moment} from 'moment';

@Component({
  selector: 'app-formatted-date-range',
  templateUrl: './formatted-date-range.component.html',
  styleUrls: ['./formatted-date-range.component.css']
})
export class FormattedDateRangeComponent {
  startTimestamp : Timestamp;
  endTimestamp : Timestamp;

  @Input() startClass : string = "";
  @Input() endClass : string = "";

  private readonly now = moment();

  @Input() set start(value: string) {
    const m : Moment = moment(value);
    if (m.isValid()) {
      this.startTimestamp = {
        date: m.format(this.now.year() === m.year() ? 'DD MMM[,]' : 'DD MMM [’]YY[,]'),
        time: m.format('HH:mm'),
      };
      if (this.endTimestamp && this.startTimestamp.date === this.endTimestamp.date) {
        this.endTimestamp.date = '';
      }
    } else {
      this.startTimestamp = null;
    }
  }

  @Input() set end(value: string) {
    const m : Moment = moment(value);
    if (m.isValid()) {
      this.endTimestamp = {
        date: m.format(this.now.year() === m.year() ? 'DD MMM[,]' : 'DD MMM [’]YY[,]'),
        time: m.format('HH:mm'),
      };
      if (this.startTimestamp && this.startTimestamp.date === this.endTimestamp.date) {
        this.endTimestamp.date = '';
      }
    } else {
      this.endTimestamp = null;
    }
  }
}

interface Timestamp {
  date: string,
  time: string
}
