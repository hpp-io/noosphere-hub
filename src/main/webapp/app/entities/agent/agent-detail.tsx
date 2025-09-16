import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './agent.reducer';

export const AgentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const agentEntity = useAppSelector(state => state.noospherehub.agent.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="agentDetailsHeading">
          <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.detail.title">Agent</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.id">Id</Translate>
            </span>
          </dt>
          <dd>{agentEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.name">Name</Translate>
            </span>
          </dt>
          <dd>{agentEntity.name}</dd>
          <dt>
            <span id="apiUrl">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.apiUrl">Api Url</Translate>
            </span>
          </dt>
          <dd>{agentEntity.apiUrl}</dd>
          <dt>
            <span id="apiKey">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.apiKey">Api Key</Translate>
            </span>
          </dt>
          <dd>{agentEntity.apiKey}</dd>
          <dt>
            <span id="statusCode">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.statusCode">Status Code</Translate>
            </span>
          </dt>
          <dd>{agentEntity.statusCode}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.description">Description</Translate>
            </span>
          </dt>
          <dd>{agentEntity.description}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{agentEntity.createdAt ? <TextFormat value={agentEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{agentEntity.updatedAt ? <TextFormat value={agentEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.createdByUser">Created By User</Translate>
          </dt>
          <dd>{agentEntity.createdByUser ? agentEntity.createdByUser.id : ''}</dd>
          <dt>
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.updatedByUser">Updated By User</Translate>
          </dt>
          <dd>{agentEntity.updatedByUser ? agentEntity.updatedByUser.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/noospherehub/agent" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/noospherehub/agent/${agentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AgentDetail;
