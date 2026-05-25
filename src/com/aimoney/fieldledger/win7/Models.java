package com.aimoney.fieldledger.win7;

import java.util.UUID;

final class Models {
    private Models() {
    }

    static String id() {
        return UUID.randomUUID().toString();
    }

    static String val(String[] values, int index) {
        return index < values.length ? values[index] : "";
    }

    static long longVal(String[] values, int index) {
        String value = val(values, index);
        return value.isEmpty() ? 0L : Long.parseLong(value);
    }

    static double doubleVal(String[] values, int index) {
        String value = val(values, index);
        return value.isEmpty() ? 0D : Double.parseDouble(value);
    }

    static int intVal(String[] values, int index) {
        String value = val(values, index);
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    static final class Manager {
        String id;
        String name;
        String alias;
        String phone;
        String note;

        Manager(String name, String alias, String phone, String note) {
            this.id = id();
            this.name = name;
            this.alias = alias;
            this.phone = phone;
            this.note = note;
        }

        Manager(String[] values) {
            this.id = val(values, 0);
            this.name = val(values, 1);
            this.alias = val(values, 2);
            this.phone = val(values, 3);
            this.note = val(values, 4);
        }

        String[] values() {
            return new String[] { id, name, alias, phone, note };
        }
    }

    static final class Plot {
        String id;
        String code;
        String name;
        String managerId;
        double area;
        String cropName;
        String note;

        Plot(String code, String name, String managerId, double area, String cropName, String note) {
            this.id = id();
            this.code = code;
            this.name = name;
            this.managerId = managerId;
            this.area = area;
            this.cropName = cropName;
            this.note = note;
        }

        Plot(String[] values) {
            this.id = val(values, 0);
            this.code = val(values, 1);
            this.name = val(values, 2);
            this.managerId = val(values, 3);
            this.area = doubleVal(values, 4);
            this.cropName = val(values, 5);
            this.note = val(values, 6);
        }

        String[] values() {
            return new String[] { id, code, name, managerId, String.valueOf(area), cropName, note };
        }
    }

    static final class Material {
        String id;
        String name;
        String category;
        String unit;
        long referencePriceCents;
        boolean enabled;
        String note;

        Material(String name, String category, String unit, long referencePriceCents, boolean enabled, String note) {
            this.id = id();
            this.name = name;
            this.category = category;
            this.unit = unit;
            this.referencePriceCents = referencePriceCents;
            this.enabled = enabled;
            this.note = note;
        }

        Material(String[] values) {
            this.id = val(values, 0);
            this.name = val(values, 1);
            this.category = val(values, 2);
            this.unit = val(values, 3);
            this.referencePriceCents = longVal(values, 4);
            this.enabled = !"0".equals(val(values, 5));
            this.note = val(values, 6);
        }

        String[] values() {
            return new String[] { id, name, category, unit, String.valueOf(referencePriceCents), enabled ? "1" : "0", note };
        }
    }

    static final class PlotInput {
        String id;
        String date;
        String plotId;
        String category;
        String materialId;
        String name;
        double quantity;
        String unit;
        long unitPriceCents;
        String note;

        PlotInput(String date, String plotId, String category, String materialId, String name, double quantity, String unit, long unitPriceCents, String note) {
            this.id = id();
            this.date = date;
            this.plotId = plotId;
            this.category = category;
            this.materialId = materialId;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.unitPriceCents = unitPriceCents;
            this.note = note;
        }

        PlotInput(String[] values) {
            this.id = val(values, 0);
            this.date = val(values, 1);
            this.plotId = val(values, 2);
            this.category = val(values, 3);
            this.materialId = val(values, 4);
            this.name = val(values, 5);
            this.quantity = doubleVal(values, 6);
            this.unit = val(values, 7);
            this.unitPriceCents = longVal(values, 8);
            this.note = val(values, 9);
        }

        long totalCents() {
            return Money.amount(quantity, unitPriceCents);
        }

        String[] values() {
            return new String[] { id, date, plotId, category, materialId, name, String.valueOf(quantity), unit, String.valueOf(unitPriceCents), note };
        }
    }

    static final class LaborRecord {
        String id;
        String date;
        String plotId;
        String projectName;
        double maleCount;
        long malePriceCents;
        double femaleCount;
        long femalePriceCents;
        long vehicleAmountCents;
        String note;

        LaborRecord(String date, String plotId, String projectName, double maleCount, long malePriceCents, double femaleCount, long femalePriceCents, long vehicleAmountCents, String note) {
            this.id = id();
            this.date = date;
            this.plotId = plotId;
            this.projectName = projectName;
            this.maleCount = maleCount;
            this.malePriceCents = malePriceCents;
            this.femaleCount = femaleCount;
            this.femalePriceCents = femalePriceCents;
            this.vehicleAmountCents = vehicleAmountCents;
            this.note = note;
        }

        LaborRecord(String[] values) {
            this.id = val(values, 0);
            this.date = val(values, 1);
            this.plotId = val(values, 2);
            this.projectName = val(values, 3);
            this.maleCount = doubleVal(values, 4);
            this.malePriceCents = longVal(values, 5);
            this.femaleCount = doubleVal(values, 6);
            this.femalePriceCents = longVal(values, 7);
            this.vehicleAmountCents = longVal(values, 8);
            this.note = val(values, 9);
        }

        long maleAmountCents() {
            return Money.amount(maleCount, malePriceCents);
        }

        long femaleAmountCents() {
            return Money.amount(femaleCount, femalePriceCents);
        }

        long totalCents() {
            return maleAmountCents() + femaleAmountCents() + vehicleAmountCents;
        }

        String[] values() {
            return new String[] { id, date, plotId, projectName, String.valueOf(maleCount), String.valueOf(malePriceCents), String.valueOf(femaleCount), String.valueOf(femalePriceCents), String.valueOf(vehicleAmountCents), note };
        }
    }

    static final class Shipment {
        String id;
        String date;
        String plotId;
        int batchNo;
        double grossWeightKg;
        double basketCount;
        double basketWeight;
        String deductionName;
        double deductionWeight;
        long unitPriceCents;
        String note;

        Shipment(String date, String plotId, int batchNo, double grossWeightKg, double basketCount, double basketWeight, String deductionName, double deductionWeight, long unitPriceCents, String note) {
            this.id = id();
            this.date = date;
            this.plotId = plotId;
            this.batchNo = batchNo;
            this.grossWeightKg = grossWeightKg;
            this.basketCount = basketCount;
            this.basketWeight = basketWeight;
            this.deductionName = deductionName;
            this.deductionWeight = deductionWeight;
            this.unitPriceCents = unitPriceCents;
            this.note = note;
        }

        Shipment(String[] values) {
            this.id = val(values, 0);
            this.date = val(values, 1);
            this.plotId = val(values, 2);
            this.batchNo = intVal(values, 3);
            this.grossWeightKg = doubleVal(values, 4);
            this.basketCount = doubleVal(values, 5);
            this.basketWeight = doubleVal(values, 6);
            this.deductionName = val(values, 7);
            this.deductionWeight = doubleVal(values, 8);
            this.unitPriceCents = longVal(values, 9);
            this.note = val(values, 10);
        }

        double grossWeightJin() {
            return grossWeightKg * 2D;
        }

        double tareWeight() {
            return basketCount * basketWeight;
        }

        double netWeightJin() {
            return grossWeightJin() - tareWeight() - deductionWeight;
        }

        long totalCents() {
            return Money.amount(netWeightJin(), unitPriceCents);
        }

        String[] values() {
            return new String[] { id, date, plotId, String.valueOf(batchNo), String.valueOf(grossWeightKg), String.valueOf(basketCount), String.valueOf(basketWeight), deductionName, String.valueOf(deductionWeight), String.valueOf(unitPriceCents), note };
        }
    }

    static final class PublicExpense {
        String id;
        String date;
        String category;
        String name;
        double quantity;
        String unit;
        long unitPriceCents;
        String note;

        PublicExpense(String date, String category, String name, double quantity, String unit, long unitPriceCents, String note) {
            this.id = id();
            this.date = date;
            this.category = category;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.unitPriceCents = unitPriceCents;
            this.note = note;
        }

        PublicExpense(String[] values) {
            this.id = val(values, 0);
            this.date = val(values, 1);
            this.category = val(values, 2);
            this.name = val(values, 3);
            this.quantity = doubleVal(values, 4);
            this.unit = val(values, 5);
            this.unitPriceCents = longVal(values, 6);
            this.note = val(values, 7);
        }

        long totalCents() {
            return Money.amount(quantity, unitPriceCents);
        }

        String[] values() {
            return new String[] { id, date, category, name, String.valueOf(quantity), unit, String.valueOf(unitPriceCents), note };
        }
    }

    static final class FixedExpense {
        String id;
        String date;
        String category;
        String name;
        long amountCents;
        int usefulLifeMonths;
        String note;

        FixedExpense(String date, String category, String name, long amountCents, int usefulLifeMonths, String note) {
            this.id = id();
            this.date = date;
            this.category = category;
            this.name = name;
            this.amountCents = amountCents;
            this.usefulLifeMonths = usefulLifeMonths;
            this.note = note;
        }

        FixedExpense(String[] values) {
            this.id = val(values, 0);
            this.date = val(values, 1);
            this.category = val(values, 2);
            this.name = val(values, 3);
            this.amountCents = longVal(values, 4);
            this.usefulLifeMonths = intVal(values, 5);
            this.note = val(values, 6);
        }

        String[] values() {
            return new String[] { id, date, category, name, String.valueOf(amountCents), String.valueOf(usefulLifeMonths), note };
        }
    }
}
