import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Container e2e test', () => {
  const containerPageUrl = '/noospherehub/container';
  const containerPageUrlPattern = new RegExp('/noospherehub/container(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const containerSample = {
    walletAddress: 'since swathe even',
    price: 4407.01,
    statusCode: 'for',
    parameters: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    createdAt: '2025-09-15T16:08:07.806Z',
  };

  let container;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/noospherehub/api/containers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/noospherehub/api/containers').as('postEntityRequest');
    cy.intercept('DELETE', '/services/noospherehub/api/containers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (container) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/noospherehub/api/containers/${container.id}`,
      }).then(() => {
        container = undefined;
      });
    }
  });

  it('Containers menu should load Containers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('noospherehub/container');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Container').should('exist');
    cy.url().should('match', containerPageUrlPattern);
  });

  describe('Container page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(containerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Container page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/noospherehub/container/new$'));
        cy.getEntityCreateUpdateHeading('Container');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', containerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/noospherehub/api/containers',
          body: containerSample,
        }).then(({ body }) => {
          container = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/noospherehub/api/containers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/noospherehub/api/containers?page=0&size=20>; rel="last",<http://localhost/services/noospherehub/api/containers?page=0&size=20>; rel="first"',
              },
              body: [container],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(containerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Container page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('container');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', containerPageUrlPattern);
      });

      it('edit button click should load edit Container page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Container');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', containerPageUrlPattern);
      });

      it('edit button click should load edit Container page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Container');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', containerPageUrlPattern);
      });

      it('last delete button click should delete instance of Container', () => {
        cy.intercept('GET', '/services/noospherehub/api/containers/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('container').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', containerPageUrlPattern);

        container = undefined;
      });
    });
  });

  describe('new Container page', () => {
    beforeEach(() => {
      cy.visit(`${containerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Container');
    });

    it('should create an instance of Container', () => {
      cy.get(`[data-cy="name"]`).type('impostor');
      cy.get(`[data-cy="name"]`).should('have.value', 'impostor');

      cy.get(`[data-cy="walletAddress"]`).type('pace sadly');
      cy.get(`[data-cy="walletAddress"]`).should('have.value', 'pace sadly');

      cy.get(`[data-cy="price"]`).type('14763.65');
      cy.get(`[data-cy="price"]`).should('have.value', '14763.65');

      cy.get(`[data-cy="statusCode"]`).type('fat');
      cy.get(`[data-cy="statusCode"]`).should('have.value', 'fat');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="parameters"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="parameters"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="createdAt"]`).type('2025-09-16T02:32');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-09-16T02:32');

      cy.get(`[data-cy="updatedAt"]`).type('2025-09-15T04:58');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2025-09-15T04:58');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        container = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', containerPageUrlPattern);
    });
  });
});
