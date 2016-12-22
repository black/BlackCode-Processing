import java.io.IOException;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapAddress;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.core.PcapStat;
import org.pcap4j.core.Pcaps;
import org.pcap4j.util.ByteArrays;
import org.pcap4j.util.NifSelector; 


void setup() {
  size(300, 300);
}

void draw() {
  background(-1);
}

class GetNextRawPacket {
  String COUNT_KEY, READ_TIMEOUT_KEY, SNAPLEN_KEY, BUFFER_SIZE_KEY, NIF_NAME_KEY, NIF_NAME;
  int COUNT, READ_TIMEOUT, SNAPLEN, BUFFER_SIZE ;

  GetNextRawPacket() {
    COUNT_KEY = GetNextRawPacket.class.getName() + ".count";
    COUNT = Integer.getInteger(COUNT_KEY, 5);
    READ_TIMEOUT_KEY = GetNextRawPacket.class.getName() + ".readTimeout";
    READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]
    SNAPLEN_KEY = GetNextRawPacket.class.getName() + ".snaplen";
    SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]
    BUFFER_SIZE_KEY = GetNextRawPacket.class.getName() + ".bufferSize";
    BUFFER_SIZE = Integer.getInteger(BUFFER_SIZE_KEY, 1 * 1024 * 1024); // [bytes]
    NIF_NAME_KEY = GetNextRawPacket.class.getName() + ".nifName";
    NIF_NAME= System.getProperty(NIF_NAME_KEY);
  }

  void main(String[] args) throws PcapNativeException, NotOpenException {
    String filter = args.length != 0 ? args[0] : "";

    println(COUNT_KEY + ": " + COUNT);
    println(READ_TIMEOUT_KEY + ": " + READ_TIMEOUT);
    println(SNAPLEN_KEY + ": " + SNAPLEN);
    println(BUFFER_SIZE_KEY + ": " + BUFFER_SIZE);
    println(NIF_NAME_KEY + ": " + NIF_NAME);
    println("\n");

    PcapNetworkInterface nif;
    if (NIF_NAME != null) {
      nif = Pcaps.getDevByName(NIF_NAME);
    } else {
      try {
        nif = new NifSelector().selectNetworkInterface();
      } 
      catch (IOException e) {
        e.printStackTrace();
        return;
      }

      if (nif == null) {
        return;
      }
    }

    println(nif.getName() + " (" + nif.getDescription() + ")");
    for (PcapAddress addr : nif.getAddresses ()) {
      if (addr.getAddress() != null) {
        println("IP address: " + addr.getAddress());
      }
    }
    println("");

    PcapHandle handle = new PcapHandle.Builder(nif.getName()).snaplen(SNAPLEN).promiscuousMode(PromiscuousMode.PROMISCUOUS)
      .timeoutMillis(READ_TIMEOUT).bufferSize(BUFFER_SIZE).build();

    handle.setFilter(filter, BpfCompileMode.OPTIMIZE);

    int num = 0;
    while (true) {
      byte[] packet = handle.getNextRawPacket();
      if (packet == null) {
        continue;
      } else {
        println(handle.getTimestamp());
        println(ByteArrays.toHexString(packet, " "));
        num++;
        if (num >= COUNT) {
          break;
        }
      }
    }

    PcapStat ps = handle.getStats();
    println("ps_recv: " + ps.getNumPacketsReceived());
    println("ps_drop: " + ps.getNumPacketsDropped());
    println("ps_ifdrop: " + ps.getNumPacketsDroppedByIf());
    handle.close();
  }
}

