import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClienteAtualComponent } from './cliente-atual.component';

describe('ClienteAtualComponent', () => {
  let component: ClienteAtualComponent;
  let fixture: ComponentFixture<ClienteAtualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClienteAtualComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClienteAtualComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
