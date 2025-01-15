import {Component} from '@angular/core';
import {View} from "src/app/common/view";
import {Handler} from "src/app/common/handler";
import {IncidentId, OfferDetails} from '@pitstop/typescriptmodels/pitstop';
import {AppContext} from '../../../app-context';
import moment from 'moment/moment';

@Component({
  selector: 'app-offer-modal',
  templateUrl: './offer-modal.component.html',
  styleUrls: ['./offer-modal.component.scss']
})
@Handler()
export class OfferModalComponent extends View {
  protected readonly moment = moment;

  incidentId: IncidentId;
  details: OfferDetails = {operatorId: AppContext.userProfile.operator}
  updateInfo = () => this.sendCommand(`/api/incidents/${this.incidentId}/offers`, this.details);
}
