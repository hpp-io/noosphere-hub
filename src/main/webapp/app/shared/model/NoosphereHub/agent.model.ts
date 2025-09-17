import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IAgent {
  id?: string;
  name?: string | null;
  apiUrl?: string;
  apiKey?: string;
  statusCode?: string;
  description?: string | null;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  createdByUser?: IUser | null;
  updatedByUser?: IUser | null;
}

export const defaultValue: Readonly<IAgent> = {};
