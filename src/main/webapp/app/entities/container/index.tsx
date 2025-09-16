import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Container from './container';
import ContainerDetail from './container-detail';
import ContainerUpdate from './container-update';
import ContainerDeleteDialog from './container-delete-dialog';

const ContainerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Container />} />
    <Route path="new" element={<ContainerUpdate />} />
    <Route path=":id">
      <Route index element={<ContainerDetail />} />
      <Route path="edit" element={<ContainerUpdate />} />
      <Route path="delete" element={<ContainerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ContainerRoutes;
