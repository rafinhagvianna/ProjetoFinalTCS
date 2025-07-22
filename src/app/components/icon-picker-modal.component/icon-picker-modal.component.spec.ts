import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IconPickerModalComponent } from './icon-picker-modal.component';

describe('IconPickerModalComponent', () => {
  let component: IconPickerModalComponent;
  let fixture: ComponentFixture<IconPickerModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IconPickerModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IconPickerModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
