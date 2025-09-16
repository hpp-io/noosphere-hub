import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IContainer {
  id?: string;
  name?: string | null;
  walletAddress?: string;
  price?: number;
  statusCode?: string;
  description?: string | null;
  parameters?: string;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  createdByUser?: IUser | null;
  updatedByUser?: IUser | null;
}

export const defaultValue: Readonly<IContainer> = {};
