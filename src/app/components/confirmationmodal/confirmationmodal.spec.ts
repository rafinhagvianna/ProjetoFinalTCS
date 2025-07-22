import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Confirmationmodal } from './confirmationmodal';

describe('Confirmationmodal', () => {
  let component: Confirmationmodal;
  let fixture: ComponentFixture<Confirmationmodal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Confirmationmodal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Confirmationmodal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
