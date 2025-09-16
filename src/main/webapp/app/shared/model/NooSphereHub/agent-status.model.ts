import dayjs from 'dayjs';
import { IAgent } from 'app/shared/model/NooSphereHub/agent.model';

export interface IAgentStatus {
  id?: string;
  createdAt?: dayjs.Dayjs;
  lastKeepAliveAt?: dayjs.Dayjs | null;
  agent?: IAgent | null;
}

export const defaultValue: Readonly<IAgentStatus> = {};
