import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/shared/reducers/user-management';
import { createEntity, getEntity, updateEntity } from './container.reducer';

export const ContainerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const containerEntity = useAppSelector(state => state.noospherehub.container.entity);
  const loading = useAppSelector(state => state.noospherehub.container.loading);
  const updating = useAppSelector(state => state.noospherehub.container.updating);
  const updateSuccess = useAppSelector(state => state.noospherehub.container.updateSuccess);

  const handleClose = () => {
    navigate('/noospherehub/container');
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
    if (values.price !== undefined && typeof values.price !== 'number') {
      values.price = Number(values.price);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...containerEntity,
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
          ...containerEntity,
          createdAt: convertDateTimeFromServer(containerEntity.createdAt),
          updatedAt: convertDateTimeFromServer(containerEntity.updatedAt),
          createdByUser: containerEntity?.createdByUser?.id,
          updatedByUser: containerEntity?.updatedByUser?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="nooSphereHubApp.nooSphereHubContainer.home.createOrEditLabel" data-cy="ContainerCreateUpdateHeading">
            <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.home.createOrEditLabel">Create or edit a Container</Translate>
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
                  id="container-id"
                  label={translate('nooSphereHubApp.nooSphereHubContainer.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.name')}
                id="container-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.walletAddress')}
                id="container-walletAddress"
                name="walletAddress"
                data-cy="walletAddress"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.price')}
                id="container-price"
                name="price"
                data-cy="price"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.statusCode')}
                id="container-statusCode"
                name="statusCode"
                data-cy="statusCode"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 20, message: translate('entity.validation.maxlength', { max: 20 }) },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.description')}
                id="container-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.parameters')}
                id="container-parameters"
                name="parameters"
                data-cy="parameters"
                type="textarea"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.createdAt')}
                id="container-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('nooSphereHubApp.nooSphereHubContainer.updatedAt')}
                id="container-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="container-createdByUser"
                name="createdByUser"
                data-cy="createdByUser"
                label={translate('nooSphereHubApp.nooSphereHubContainer.createdByUser')}
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
                id="container-updatedByUser"
                name="updatedByUser"
                data-cy="updatedByUser"
                label={translate('nooSphereHubApp.nooSphereHubContainer.updatedByUser')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/noospherehub/container" replace color="info">
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

export default ContainerUpdate;
