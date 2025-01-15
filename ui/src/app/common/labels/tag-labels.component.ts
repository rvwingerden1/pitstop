import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-tag-labels',
  templateUrl: './tag-labels.component.html',
  styleUrls: ['./tag-labels.component.scss']
})
export class TagLabelsComponent {
  @Input() labels: any[];
  @Input() itemTemplate;
  @Input() formatter;
}
