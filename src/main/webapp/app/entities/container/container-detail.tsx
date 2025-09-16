import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './container.reducer';

export const ContainerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const containerEntity = useAppSelector(state => state.noospherehub.container.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="containerDetailsHeading">
          <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.detail.title">Container</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.id">Id</Translate>
            </span>
          </dt>
          <dd>{containerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.name">Name</Translate>
            </span>
          </dt>
          <dd>{containerEntity.name}</dd>
          <dt>
            <span id="walletAddress">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.walletAddress">Wallet Address</Translate>
            </span>
          </dt>
          <dd>{containerEntity.walletAddress}</dd>
          <dt>
            <span id="price">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.price">Price</Translate>
            </span>
          </dt>
          <dd>{containerEntity.price}</dd>
          <dt>
            <span id="statusCode">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.statusCode">Status Code</Translate>
            </span>
          </dt>
          <dd>{containerEntity.statusCode}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.description">Description</Translate>
            </span>
          </dt>
          <dd>{containerEntity.description}</dd>
          <dt>
            <span id="parameters">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.parameters">Parameters</Translate>
            </span>
          </dt>
          <dd>{containerEntity.parameters}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {containerEntity.createdAt ? <TextFormat value={containerEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {containerEntity.updatedAt ? <TextFormat value={containerEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.createdByUser">Created By User</Translate>
          </dt>
          <dd>{containerEntity.createdByUser ? containerEntity.createdByUser.id : ''}</dd>
          <dt>
            <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.updatedByUser">Updated By User</Translate>
          </dt>
          <dd>{containerEntity.updatedByUser ? containerEntity.updatedByUser.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/noospherehub/container" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/noospherehub/container/${containerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ContainerDetail;
