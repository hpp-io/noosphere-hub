import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/shared/reducers/user-management';
import { createEntity, getEntity, updateEntity } from './agent.reducer';

export const AgentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const agentEntity = useAppSelector(state => state.noospherehub.agent.entity);
  const loading = useAppSelector(state => state.noospherehub.agent.loading);
  const updating = useAppSelector(state => state.noospherehub.agent.updating);
  const updateSuccess = useAppSelector(state => state.noospherehub.agent.updateSuccess);

  const handleClose = () => {
    navigate('/noospherehub/agent');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...agentEntity,
      ...values,
      createdByUser: users.find(it => it.id.toString() === values.createdByUser?.toString()),
      updatedByUser: users.find(it => it.id.toString() === values.updatedByUser?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          ...agentEntity,
          createdAt: convertDateTimeFromServer(agentEntity.createdAt),
          updatedAt: convertDateTimeFromServer(agentEntity.updatedAt),
          createdByUser: agentEntity?.createdByUser?.id,
          updatedByUser: agentEntity?.updatedByUser?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nooSphereHubApp.nooSphereHubAgent.home.createOrEditLabel" data-cy="AgentCreateUpdateHeading">
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.home.createOrEditLabel">Create or edit a Agent</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="agent-id"
                  label={translate('nooSphereHubApp.nooSphereHubAgent.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgent.name')}
                id="agent-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgent.apiUrl')}
                id="agent-apiUrl"
                name="apiUrl"
                data-cy="apiUrl"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 1024, message: translate('entity.validation.maxlength', { max: 1024 }) },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgent.apiKey')}
                id="agent-apiKey"
                name="apiKey"
                data-cy="apiKey"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgent.statusCode')}
                id="agent-statusCode"
                name="statusCode"
                data-cy="statusCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgent.description')}
                id="agent-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgent.createdAt')}
                id="agent-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubAgent.updatedAt')}
                id="agent-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="agent-createdByUser"
                name="createdByUser"
                data-cy="createdByUser"
                label={translate('nooSphereHubApp.nooSphereHubAgent.createdByUser')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="agent-updatedByUser"
                name="updatedByUser"
                data-cy="updatedByUser"
                label={translate('nooSphereHubApp.nooSphereHubAgent.updatedByUser')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/noospherehub/agent" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default AgentUpdate;
