package mmbn.multi;

import java.util.ArrayList;
import java.util.List;
import mmbn.prod.ProgramAdvanceProducer;
import mmbn.types.BattleChip;
import mmbn.types.ProgramAdvance;
import rand.ByteStream;
import rand.Library;

public abstract class AbstractBN456ProgramAdvanceProducer extends ProgramAdvanceProducer {
	protected final Library<BattleChip> chipLibrary;

	public AbstractBN456ProgramAdvanceProducer(final Library<ProgramAdvance> paLibrary,
			final Library<BattleChip> chipLibrary,
			ProgramAdvance.Type[] types) {
		super(paLibrary, types);
		this.chipLibrary = chipLibrary;
	}
	
	@Override
	public int getDataSize() {
		return -1;
	}
	
    @Override
	public ProgramAdvance deferredReadFromStream(ByteStream stream) {
		// Read chip count and PA type.
		int chipCount = stream.readUInt8();
		ProgramAdvance.Type type = typeFromIndex(stream.readUInt8());

		// Read PA result.
		BattleChip result = this.chipLibrary.getElement(stream.readUInt16());

		// Check PA type.
		switch (type) {
			case CONSECUTIVE:
				BattleChip chip = this.chipLibrary.getElement(
						stream.readUInt16());
				return new ProgramAdvance(result, chip, chipCount);
			case COMBINATION:
				List<BattleChip> chips = new ArrayList<>(chipCount);
				for (int i = 0; i < chipCount; i++) {
					chips.add(this.chipLibrary.getElement(stream.readUInt16()));
				}
				return new ProgramAdvance(result, chips);
			default:
				throw new IllegalArgumentException("Unknown PA type.");
		}
	}

	@Override
	public void writeToStream(ByteStream stream, ProgramAdvance pa) {
		stream.writeUInt8((short) pa.chipCount());
		stream.writeUInt8((short) indexFromType(pa.type()));
		stream.writeUInt16(pa.result().index());

		for (BattleChip chip : pa.chips()) {
			stream.writeUInt16(chip.index());
		}
	}
}
