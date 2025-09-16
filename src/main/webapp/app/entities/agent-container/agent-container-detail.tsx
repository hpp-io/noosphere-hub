import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './agent-container.reducer';

export const AgentContainerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const agentContainerEntity = useAppSelector(state => state.noospherehub.agentContainer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="agentContainerDetailsHeading">
          <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.detail.title">AgentContainer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.id">Id</Translate>
            </span>
          </dt>
          <dd>{agentContainerEntity.id}</dd>
          <dt>
            <span id="statusCode">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.statusCode">Status Code</Translate>
            </span>
          </dt>
          <dd>{agentContainerEntity.statusCode}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>
            {agentContainerEntity.createdAt ? (
              <TextFormat value={agentContainerEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>
            {agentContainerEntity.updatedAt ? (
              <TextFormat value={agentContainerEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.node">Node</Translate>
          </dt>
          <dd>{agentContainerEntity.node ? agentContainerEntity.node.id : ''}</dd>
          <dt>
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgentContainer.container">Container</Translate>
          </dt>
          <dd>{agentContainerEntity.container ? agentContainerEntity.container.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/noospherehub/agent-container" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/noospherehub/agent-container/${agentContainerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AgentContainerDetail;
