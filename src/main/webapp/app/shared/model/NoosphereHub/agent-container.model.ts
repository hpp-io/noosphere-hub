import dayjs from 'dayjs';
import { IAgent } from 'app/shared/model/NoosphereHub/agent.model';
import { IContainer } from 'app/shared/model/NoosphereHub/container.model';

export interface IAgentContainer {
  id?: string;
  statusCode?: string;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  node?: IAgent | null;
  container?: IContainer | null;
}

export const defaultValue: Readonly<IAgentContainer> = {};
